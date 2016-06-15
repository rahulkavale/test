(ns mytweeter.models.hashtag-test
  (:require [midje.sweet :refer :all])
  (:require [mytweeter.models.hashtags :as hashtags]))

(fact "Extract hashtags from tweet and tweet body string"
      (hashtags/extract-hashtags "Tweet for #clojure #helpshift #clojure") =>
      ["#clojure" "#helpshift"]
      (hashtags/extract-hashtags {:body "A #new #tweet-with-hashtag"}) =>
      ["#new" "#tweet-with-hashtag"])
