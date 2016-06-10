(ns mytweeter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as ring]
            [mytweeter.migrations.migration :as migration]
            [mytweeter.controllers.tweet-controller :as tweet-controller]))

(defroutes app-routes
  (GET "/tweets" [] (tweet-controller/get-all-tweets))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))

(defn start [port]
  (ring/run-jetty app {:port port}))

(defn -main []
  (migration/migrate)
  (let [port (Integer. "8000")]
    (start port)))
