(ns pharchive-api.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [pharchive-api.db.schema :as schema]))

(defdb db schema/db-spec)

(def entities ["users" "collections"])

(defentity users)
(defentity collections)

;; TODO: I need a presenter namespace
(defn collections-presenter [collection-hash]
  (let [keys
        [:description
         :frame
         :film
         :taken_at
         :quantity_frames
         :location
         ]] (select-keys collection-hash keys))
  )

(defn create [table value]
  (insert table
          (values value)))

(defn update-record [table m]
  "by is a map of key values"
  (update table
          (set-fields (:values m))
          (where (:by m))))

(defn get-all-records [table lim off]
  (select table
          (limit lim)
          (offset off)))

(defn get-records-by-id [table value l]
  (first (select table
                 (where {:id value})
                 (limit l))))

(defn get-record [table value]
  (first (get-records-by-id table value 1)))

(defn create-record [table value]
  (insert table
          (values value)))

(defn delete-record [table value]
  (delete table
          (where value)))

;; (defn update-user [id first-name last-name email]
;;   (update users
;;   (set-fields {:first_name first-name
;;                :last_name last-name
;;                :email email})
;;   (where {:id id})))

;; (defn get-user [id]
;;   (first (select users
;;                  (where {:id id})
;;                  (limit 1))))


(defn get-collection [id]
  (first (select collections
                 (where {:uuid (java.util.UUID/fromString id)})
                 (limit 1))))
