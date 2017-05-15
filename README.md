![FastLSH_LOGO](https://cloud.githubusercontent.com/assets/11495951/24863723/9f84929a-1e34-11e7-8689-d2151bc1aadd.png)
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
Mark sure `Spark` is installed and `spark-shell` is in `PATH`, you can find install guide [here](http://spark.apache.org/downloads.html) .

`spark-shell -i ./src/FastLSH.scala`

check the result in `./testData/candidateSet.csv` or the outputPath you set


### Have a glimsp 
![FastLSH_LOGO](https://cloud.githubusercontent.com/assets/11495951/26063431/9ee9ec2a-39c0-11e7-812f-bf19e1cca278.png)

