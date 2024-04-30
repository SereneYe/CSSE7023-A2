#!/bin/bash

if [ -f submission.zip ]; then
  old=$(date +%s)
  echo "Backup up old submission as submission-${old}"
  echo
  mv submission.zip submission-${old}.zip
fi

zip submission.zip src/sheep/expression/arithmetic/Mean.java
zip submission.zip src/sheep/expression/arithmetic/Median.java
zip submission.zip src/sheep/parsing/ComplexParser.java
zip submission.zip src/sheep/parsing/*
zip submission.zip src/sheep/ui/graphical/javafx/SheepApplication.java
zip submission.zip src/sheep/ui/graphical/javafx/*

zip submission.zip test/sheep/parsing/ComplexScannerTest.java
zip submission.zip test/sheep/expression/arithmetic/MeanTest.java
zip submission.zip test/sheep/expression/arithmetic/MedianTest.java

zip submission.zip refs.md
