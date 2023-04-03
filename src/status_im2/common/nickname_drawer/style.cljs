(ns status-im2.common.nickname-drawer.style
  (:require [quo2.foundations.colors :as colors]))

(defn context-container
  []
  {:flex-direction   :row
   :background-color (colors/theme-colors colors/neutral-10 colors/neutral-80)
   :border-radius    20
   :align-items      :center
   :align-self       :flex-start
   :padding          4
   :margin-top       8
   :margin-left      -4
   :margin-bottom    16})

(def buttons-container
  {:margin-top      20
   :flex-direction  :row
   :align-items     :center
   :justify-content :space-evenly})

(def cancel-button
  {:flex         1
   :margin-right 12})

(def action-button
  {:flex 1})

(def nickname-container
  {:margin-horizontal 20})

(defn nickname-description
  []
  {:margin-left 4
   :color       (colors/theme-colors colors/neutral-50 colors/neutral-40)})

(def nickname-description-container
  {:flex-direction :row
   :align-items    :center
   :margin-top     8})
