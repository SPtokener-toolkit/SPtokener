# SPtokener

## About

This folder contains SPtokener implementation, written on Java.  

## Build and Run

Working with Java 21 and Gradle 8.5. Previous versions might not be supported.

If you want simply run this programm, you can use [build_run](./build_run.sh) script like this

```
sh build_run.sh <path_to_src_directory> <bcb-mode> <beta> <theta> <eta>
```

bcb-mode there is either --bcb or --no-bcb. In bcb mode our tool will not find clones in different directories. It also changes output file format

beta, theta, eta are hyperparameters. We recommend to use beta = 0.5, theta = 0.35, eta = 0.6

If you want to tune java running arguments, you can use gradle commands.

## Current results

Recall (%) on BCB compared to CCStokener

| Tool | T1 | T2 | VST3 | ST3 | MT3 | WT3/T4 |
|------|----|----|------|-----|-----|--------|
|SPtokener| 97 | 85 | 94 | 83 | 55 | 13
|CCStokener| 100 | 99 | 98 | 92 | 53 | 2.3
