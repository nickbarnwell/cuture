(ns cuture.handler
  (:use compojure.core)
  (:use [cuture.tumblr :only (fetch-corgis)])
  (:use ring.adapter.jetty)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "Here be Corgis. Try hitting /random")
  (GET "/random" [] (fetch-corgis))
  (GET ["/random/:num" :num #"[0-9]"] [num]
       (clojure.string/join "," (fetch-corgis (Integer/parseInt num))))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main [& args]
 (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (run-jetty app {:port port})))
