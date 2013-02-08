(ns cuture.handler
  (:use compojure.core)
  (:use [cuture.tumblr :only (get-corgis)])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "Here be Corgis. Try hitting /random")
  (GET "/random" [] (get-corgis))
  (GET ["/random/:num" :num #"[0-9]"] [num]
       (get-corgis num))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
