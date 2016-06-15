(defproject mytweeter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.5.0"]
                 [ring/ring-defaults "0.1.5"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.6.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :main mytweeter.handler
  :plugins [[lein-ring "0.9.7"]
            [lein-midje "3.2"]]
  :ring {:handler mytweeter.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]
                        [midje "1.8.3"]]}})
