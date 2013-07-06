(ns actions-test
  (:require [expectations :refer :all]
            [mario.actions :as actions]))

;; Creating without a name
(expect {:status 200} (actions/create-job "some-job"))

;; Building

;; 201s with location for a job that exists
(expect {:status 201
         :headers {"Location" "/jobs/some-job/builds/1"}}
        (actions/build "some-job"))

;; 404s for a job that doesn't exist
(expect 404 (:status (actions/build "non-existent-job")))
