(ns pharchive-api.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [liberator.core :refer :all]
            [pharchive-api.db.core :as db]
            [cheshire.core :as json]
          ))

(defn construct-hypermedia-response [metadata data]
  {
   :meta metadata
   :data data})

(defn api-response [meta data]
  (json/generate-string (construct-hypermedia-response meta data)))

(defresource list-collections [limit offset]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (api-response {:limit  (Integer/parseInt limit)
                            :offset (Integer/parseInt offset)
                            :links  {:self "http://localhost:3000" }} (db/get-all-records :collections limit offset)) )

(defresource collection [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (json/generate-string (construct-hypermedia-response {} (db/get-collection (Integer/parseInt id)))))


(defroutes app-routes
  (ANY "/collections" [:as req]
       ;; TODO: This is quite dirty
       (let [params (req :params)
             l      (params :limit)
             o      (params :offset)
             limit  (if l l "10")
             offset (if o o "0")]
         (list-collections limit offset)))

  (ANY "/collection/:id" [id]
       (collection id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
