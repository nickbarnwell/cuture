(ns cuture.tumblr.posts
  (:require [clj-http.client :as client :only (request)])
  (:use [cuture.tumblr.core :only (tumblr-request)]))

(defn extract-posts [resp]
  "Extract Posts collection from Tumblr API response."
  (get-in resp [:body :response]))

(defn posts-tagged
  "Returns a lazy seq of posts tagged with [tags] from tumblr API.
  Chunks are 20 items each."
  ([tags] (let [tags {"tag" (clojure.string/join (map name tags))}
        req (tumblr-request :get [:tagged] tags)
        update-req (fn [req before]
                     (assoc-in req [:query-params :before] before))
        exec-req-one (fn exec-req-one [req]
                       (-> req client/request extract-posts))
        exec-request (fn exec-request [req]
                       (let [resp (exec-req-one req)
                             before (-> resp peek :timestamp)]
                         (let [new-req (update-req req before)]
                           (lazy-cat resp (exec-request new-req))
                           )))]
    (exec-request req))))

(defn has-photos? [post]
  (if (:photos post)
    true
    false))

(defn largest-photos-in-post [post]
  (let [photos (:photos post)]
    (for [itm photos
          :let [sizes (:alt_sizes itm)]]
      (apply max-key :width sizes))))

(defn photos-tagged [tags]
  "Lazy sequence of photos tagged with [tags]"
  (let [photos (map largest-photos-in-post
                    (filter has-photos? (posts-tagged tags)))]
  (map :url (flatten photos))))
