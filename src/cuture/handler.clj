(ns cuture.handler
  (:use compojure.core)
  (:use [cuture.tumblr.posts :only (photos-tagged)])
  (:use ring.adapter.jetty)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "Here be Corgis. Try hitting /random")
  (GET "/random" [] (first (photos-tagged [:corgi])))
  (GET ["/random/:num" :num #"[0-9]"] [num]
       (clojure.string/join "," (take (Integer/parseInt num) (photos-tagged [:corgi]))))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main [& args]
 (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (run-jetty app {:port port})))
