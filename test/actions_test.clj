(ns actions-test
  (:use expectations)
  (:require [mario.actions :as actions]))

(expect {:status 201
         :headers {"Location" "/jobs/foo/builds/foo"}}
        (actions/build "foo"))
