(ns mario.views
  (:use [hiccup page util]))

(defn index-page []
  (html5
    [:head [:title "Compojure Fun"]]
    [:body
     [:h1 (escape-html (str "Fun with Clojure and Compojure"))]
     [:a {:href "/<script>"} "Escaped HTML"]
     ]))

