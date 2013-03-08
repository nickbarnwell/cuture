(ns cuture.tumblr
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def api-key
  (let [env (System/getenv "TUMBLR_API_KEY")]
    (if env
      env
      (slurp "secret.txt"))))

;; The way this is meant to work is to have a 'store' of corgis that operates as
;; a perpetually filled queue, backfilling when it shrinks below a certain
;; threshold.

(defn create-url [positionals]
  "Takes positionals and returns appropariate endpoint URL"
  (let [base-url "http://api.tumblr.com/v2"
        components (cons base-url positionals)]
    (clojure.string/join "/" (map name components))))

(defn tumblr-request
  ([method positionals] (create-tumblr-request method positionals nil))
  ([method positionals data] (create-tumblr-request method positionals data nil))
  ([method positionals data opts]
   (let [req {
              :as :json
              :url (create-url positionals)
              :method method
              :query-params { "api_key" api-key } 
              }
         req (merge-with merge req opts)
         req (if (#{:post :put :delete} method)
               (assoc req :body (json/generate-string data))
               (assoc req :query-params (merge-with merge (req :query-params) data)))]
     req)))

(defn extract-posts [resp]
  "Extract Posts collection from Tumblr API response."
  (get-in resp [:body :response]))

(defn posts-tagged
  "Returns a lazy seq of posts tagged with [tags] from tumblr API.
  Chunks are 20 items each."
  ([tags] (let [tags {"tag" (clojure.string/join (map name tags))}
        req (create-tumblr-request :get [:tagged] tags)
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
  (->> (flatten photos) (map :url))))
