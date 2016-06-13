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
            [mytweeter.models.tweet :as tweet]
            [cheshire.core :refer :all])
  (:use ring.util.response))

(defn parse-json [httpInputStream]
  (let [body (slurp httpInputStream)]
    (parse-string body)))

(defroutes app-routes
  (POST "/tweets" {tweet :body}
        (let [json (get (parse-json tweet) "tweet")]
          (tweet-controller/create-tweet json)))
  (GET "/tweets" [] (tweet-controller/get-all-tweets))
  (DELETE "/tweets" []
          (tweet-controller/delete-all))
  (POST "/tweets/:tweet_id/retweet" {payload :body params :params}
        (let [user (parse-json payload)
              tweet-id (:tweet_id params)]
          (tweet-controller/retweet tweet-id user)))
  (GET "/users" [] (user-controller/get-all-users))
  (GET "/users/:user_id/tweets" [user_id]
       (do
         (println (str "user id " user_id))
         (user-controller/get-user-tweets user_id)))
  (POST "/users" {request :body}
        (user-controller/create-user (parse-json request)))
  (POST "/follow" {follow-info :body}
        (user-controller/follow (parse-json follow-info))))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (middleware/wrap-json-response)))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-response)))

(defn start [port]
  (ring/run-jetty app {:port port}))

(defn -main []
  (migration/migrate)
  (let [port (Integer. "8000")]
    (start port)))

