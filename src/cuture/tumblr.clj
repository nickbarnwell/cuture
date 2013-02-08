(ns cuture.tumblr
  (:require [clj-http.client :as client]))

(defn get-corgis 
  ([] (first (get-corgis 1)))
  ([n] (repeat n nil)))


