(defproject cuture "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.5"]
                 [clj-http "0.6.4"]
                 [ring/ring-jetty-adapter "0.3.8"]
                 ]
  ;:main cuture.tumblr
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler cuture.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
