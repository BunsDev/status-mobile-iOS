(ns quo2.components.list-items.account.component-spec
  (:require [test-helpers.component :as h]
            [quo2.components.list-items.account.view :as account]))

(def account-props
  {:customization-color :purple
   :emoji               "🍑"
   :name                "New House"
   :address             "0x0ah...78b"})

(def tag-props
  {:token-symbol "SNT"
   :value        1000})

(def balance-props
  {:total-value          0
   :variation-value      0
   :variation-percentage 0})

(h/describe "List items: account"
  (h/test "default render'"
    (h/render [account/view
               {:account-props account-props
                :state         :default}])
    (h/is-truthy (h/get-by-label-text :container))))
