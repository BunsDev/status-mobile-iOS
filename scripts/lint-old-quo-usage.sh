#!/usr/bin/env sh

QUO_USAGES=$(grep -r '\[quo\.' --include '*.cljs' --include '*.clj' './src/status_im2')

if [ -n "$QUO_USAGES" ]; then
    echo -e "\n\033[0;31mERROR: Usage of the old 'quo' namespace detected in 'status_im2' code. Please update to 'quo2'. \n"
    echo -e "$QUO_USAGES \033[0m"
    exit 1
fi
