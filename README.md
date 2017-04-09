# FastLSH-Spark
Spark version of FastLSH (frequency counting locality sensitive hashing)

### Change Parameters

    vim src/FastLSH.scala

change parameters between line 37-46

    val D = 57 //# of dimensions
    val L = 200 //# of group hash
    val K = 1 //# the number of hash functions in each group hash
    val N = 1000 //# of vectors in the dataset
    val Q = 1000 //# of vertors in the queryset
    val W = 1.2 //bucket width
    val T = 100 // threshold
    val setQPath = "../testData/dataset1000NoIndex.csv"
    val setNPath = "../testData/dataset1000NoIndex.csv"
    val outputPath = "../testData/candidateSet.csv"

### Execution 
Mark sure `Spark` is installed and `spark-shell` is in `PATH`, you can find install guide [here](here http://spark.apache.org/downloads.html)

`spark-shell -i ./src/FastLSH.scala`

check the result in `./testData/candidateSet.csv` or the outputPath you set




