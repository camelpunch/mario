(ns mario.views
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [clojure.string :refer [capitalize]]))

(defn- xml [& args] (html (xml-declaration "utf-8") args))
(defn- ccproject [job]
  (let [builds (:job/builds job)
        result (:build/result (first builds))
        activity (if result "sleeping" "building")]
    [:Project {:name (:job/name job)
               :activity (capitalize activity)
               :lastBuildStatus (capitalize (or result "Unknown"))}]))

(defn cctray [jobs] (xml [:Projects (map ccproject jobs)]))

