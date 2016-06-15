(ns mytweeter.utils-test
  (:require [mytweeter.utils :as utils]
            [midje.sweet :refer :all]))

(fact "Should check if string starts with the given char"
      (utils/starts-with? \? "?some-string") => true
      (utils/starts-with? \# "#hash-tag") => true
      (utils/starts-with? \a "some-random-string") => false)

