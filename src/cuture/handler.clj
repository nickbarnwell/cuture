(ns cuture.handler
  (:use compojure.core)
  (:require [cuture.tumblr :only get-corgis])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "Here be Corgis. Try hitting /random")
  (GET "/random" [] )
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
