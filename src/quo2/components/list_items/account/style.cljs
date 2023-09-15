(ns quo2.components.list-items.account.style
  (:require [quo2.foundations.colors :as colors]))

(defn- background-color
  [{:keys [state blur? customization-color]}]
  (cond (and (or (= state :pressed) (= state :selected)) (not blur?))
        (colors/custom-color customization-color 50 5)
        (and (or (= state :pressed) (= state :selected)) blur?)
        colors/white-opa-5
        (and (= state :active) (not blur?))
        (colors/custom-color customization-color 50 10)
        (and (= state :pressed) blur?) colors/white-opa-10
        :else :transparent))

(defn container
  [props]
  {:height             48
   :border-radius      12
   :background-color   (background-color props)
   :flex-direction     :row
   :align-items        :center
   :padding-horizontal 8
   :padding-vertical   6
   :justify-content    :space-between})

(def left-container {:flex-direction :row :align-items :center})
