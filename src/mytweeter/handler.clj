(ns mytweeter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.params :as rmp]
            [ring.adapter.jetty :as ring]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [mytweeter.migrations.migration :as migration]
            [mytweeter.controllers.tweet-controller :as tweet-controller]
            [mytweeter.controllers.user-controller :as user-controller]
            [mytweeter.models.tweet :as tweet])
  (:use ring.util.response))


(defroutes app-routes
  (GET "/tweets" [] (tweet-controller/get-all-tweets))
  (POST "/tweets" {tweet :body}
        (do
          (tweet-controller/create-tweet (slurp tweet))))
  (GET "/users" [] (user-controller/get-all-users)))

(def app
 ;(wrap-defaults app-routes api-defaults)
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (rmp/wrap-params)
      (middleware/wrap-json-response)))

(defn start [port]
  (ring/run-jetty app {:port port}))

(defn -main []
  (migration/migrate)
  (let [port (Integer. "8000")]
    (start port)))

