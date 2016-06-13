(ns mytweeter.models.hashtag-test
  (:require [clojure.test :refer :all])
  (:require [mytweeter.models.hashtags :as hashtags]))

(deftest should-extract-hashtags
  (testing "should extract hashtags from a string"
   (is (= '("#clojure" "#spec")
          (hashtags/extract-hashtags "It looks a bit like ADT #clojure #spec @wvdlaan "))))
  (testing "should extract hashtags from tweet map"
    (is (= '("#clojure")
           (hashtags/extract-hashtags {:body "#clojure interview question: list five reasons that clojure.core is not, and should not be, idiomatic #clojure"})))))
