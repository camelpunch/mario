(ns acceptance-test
  (:use expectations
        ring.mock.request
        mario.routes))

(expect 200 (:status (app (request :get "/"))))
