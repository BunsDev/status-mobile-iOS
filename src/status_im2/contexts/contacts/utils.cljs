(ns status-im2.contexts.contacts.utils
  (:require [status-im.contact.db :as contact.db]))

(defn contact-by-identity
  [contacts contact-identity]
  (or (get contacts contact-identity)
      (contact.db/public-key->new-contact contact-identity)))