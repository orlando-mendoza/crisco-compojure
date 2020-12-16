(defproject crisco "1.0.0-SNAPSHOT"
  :description "a project for url shortening"
  :url "http://crisco.herokuapp.com"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.6.2"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [ring/ring-devel "1.8.2"]
                 [environ "1.2.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]
            [lein-ring "0.12.5"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "crisco-standalone.jar"
  :profiles {:production {:env {:production true}}}
  :ring {:handler crisco.web/app}
  :main crisco.web)
