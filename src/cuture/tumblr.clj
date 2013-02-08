(ns cuture.tumblr
  (:require [clj-http.client :as client]))

(defn get-corgis 
  ([] (first (get-corgis 1)))
  ([n] (repeat n nil)))


(defn create-url [positionals]
  (let [base-url "http://api.tumblr.com/v2"
        components (cons base-url positionals)]
    (clojure.string/join "/" (map name components))))

(defn tumblr-request
  ([fnk positionals] (tumblr-request fnk positionals nil))
  ([fnk positionals data] (tumblr-request fnk positionals data nil))
  ([fnk positionals data opts]
    (let [url (create-url positionals)
          request-opts (merge {:as :json} opts)
          all-options (merge data request-opts)
          payload (assoc-in all-options [:query-params "api_key"] api-key)]
       (fnk url payload))))

(defn extract-posts [resp]
  "Extract Posts collection from Tumblr API response."
  (get-in resp [:body :response]))

(defn get-posts-tagged [tags]
  "Retrieve posts tagged with [tags] from tumblr API"
  (extract-posts (tumblr-request client/get [:tagged] 
    {:query-params {"tag" (clojure.string/join (map name tags))}})))

(defn largest-photos-in-post [post]
  (let [photos (:photos post)]
    (for [itm photos
          :let [sizes (:alt_sizes itm)]]
      (apply max-key :width sizes)))) 

(defn fetch-corgis 
  ([] (first (fetch-corgis 1)))
  ([n]
  "Fetches n-more corgis from tumblr"
  (let [corgis! (into [] 
                  (flatten (map 
                     largest-photos-in-post (get-posts-tagged ["corgi"]))))]
  (map :url (take n (repeatedly #(rand-nth corgis!)))))))
