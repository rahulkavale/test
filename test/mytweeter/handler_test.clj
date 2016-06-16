(ns mytweeter.handlter-test
  (:require  [midje.sweet :as midje :refer :all]
             [ring.mock.request :as mock]
             [cheshire.core :refer :all]
             [clojure.java.jdbc :as sql])
  (:require [mytweeter.test-helper :as helper]
             [mytweeter.handler :as handler]
             [mytweeter.db :as db]
             [mytweeter.migrations.migration :as migration]))

;TODO create database automatically
(background (before :facts (reset! db/config "postgresql://localhost:5432/mytweeter_test"))
            (before :facts (migration/migrate))
            (after :facts (helper/clear-tables)))

(fact "should create users"
      (let [user-request-payload (generate-string {"user" {"first_name" "superman"}})
            create-user-response (handler/app (mock/request :post "/users" user-request-payload))
            all-users (handler/app (mock/request :get "/users"))
            user-response (helper/parse all-users)]
        (:status create-user-response) => 200
        (:status all-users) => 200
        (count (get user-response "users")) => 1
        (get (first (get user-response "users")) "first_name") => "superman"))

(fact "should create tweets"
      (let [tweet-request-payload (generate-string {"tweet" {"body" "Tweet body" "user_id" "12345"}})
            create-tweet-response (handler/app (mock/request :post "/tweets" tweet-request-payload))
            all-tweets-reponse (handler/app (mock/request :get "/tweets"))
            tweets (parse-string (:body all-tweets-reponse))]
        (:status create-tweet-response) => 200
        (:status all-tweets-reponse) => 200
        (count (get tweets "tweets")) => 1
        (get (first (get tweets "tweets")) "body") => "Tweet body"))

(let [user-request-payload (generate-string {"user" {"first_name" "superman"}})
      create-user-response (handler/app (mock/request :post "/users" user-request-payload))
      tweet-request-payload (generate-string {"tweet" {"body" "Tweet body" "user_id" "12345"}})
      create-tweet-response (handler/app (mock/request :post "/tweets" tweet-request-payload))
      tweet-id (get (helper/parse create-tweet-response) "id")
      user-id (get (helper/parse create-user-response) "id")
      retweet-payload (generate-string {"user_id" user-id})
      retweet-endpoint (str "/tweets/" tweet-id "/retweet")
      retweet-response (handler/app (mock/request :post retweet-endpoint retweet-payload))
      get-retweeters-endpoint (str "/tweets/" tweet-id "/retweeters")
      retweeters-response (handler/app (mock/request :get get-retweeters-endpoint))
      retweeter-user-ids (map #(get % "user_id") (get (helper/parse retweeters-response) "retweeters"))]
  (fact "A user should be able to retweet"
        (:status create-user-response) => 200
        (:status create-tweet-response) => 200
        (:status retweet-response) => 200
        (:status retweet-response) => 200
        (:status retweeters-response) => 200
        (count retweeter-user-ids) => 1
        (first retweeter-user-ids) => user-id))
