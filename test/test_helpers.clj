(ns test-helpers
  (:import java.io.ByteArrayInputStream)
  (:use [clojure.xml :as xml]))

(defn parse-str [string]
  (->> string .trim .getBytes ByteArrayInputStream. xml/parse))

(defn parse-cctray-projects [string]
  (->> string parse-str :content (map :attrs)))

(defn project-status [projects project-name]
  (first (filter #(= project-name (:name %)) projects)))

(defn uuid [] (str (java.util.UUID/randomUUID)))
