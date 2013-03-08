(ns cuture.tumblr.core
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
  ([method positionals] (tumblr-request method positionals nil))
  ([method positionals data] (tumblr-request method positionals data nil))
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
