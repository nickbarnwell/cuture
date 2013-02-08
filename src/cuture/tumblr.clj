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
  ([fnk positionals data] (tumblr-request positionals data nil))
  ([fnk positionals data opts]
  (let [url (create-url positionals)
        data (if opts (merge data opts) data)
        final-opts (merge-with conj (assoc-in data [:query-params "api_key"] api-key))]
     (fnk url final-opts))))


