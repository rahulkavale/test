(ns mytweeter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as ring]))

(defroutes app-routes
  (GET "/tweets" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))

(defn start [port]
  (ring/run-jetty app {:port port}))

(defn -main []
  (let [port (Integer. "8000")]
    (start port)))
