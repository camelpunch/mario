(ns mario.views
  (:use [hiccup.core :refer [html]]
        [hiccup.page :refer [xml-declaration]]))

(defn xml [& args]
  (html (xml-declaration "utf-8") args))

(defn cctray [jobs]
  (xml
    [:Projects
     (map #(vec [:Project {:name (:job/name %)
                           :activity "Building"
                           :lastBuildStatus "Unknown"}])
          jobs)]))

