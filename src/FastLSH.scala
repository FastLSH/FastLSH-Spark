/***
  Copyright 2017 Yaohai XU

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ***/

/**
    FastLSH
    FastLSH.scala
    Purpose: Scala file(Spark) for FastLSH

    @author Peter Yaohai XU
    @version 1.0 4/07/17
  */

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import java.io._


    // load log and initiate spark instance
    val logFile = "/usr/lib/spark/README.md" // Should be some file on your system
    val conf = new SparkConf().setAppName("FastLSH").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // parameter sets
    val D = 57 //# of dimensions
    val L = 200 //# of group hash
    val K = 1 //# the number of hash functions in each group hash
    val N = 1000 //# of vectors in the dataset
    val Q = 1000 //# of vertors in the queryset
    val W = 1.2 //bucket width
    val T = 100 // threshold
    val setQPath = "./testData/dataset1000NoIndex.csv"
    val setNPath = "./testData/dataset1000NoIndex.csv"
    val outputPath = "./testData/candidateSet.csv"
    val r = scala.util.Random


    val sparkSession = SparkSession.builder.master("local").appName("FastLSH").getOrCreate()

    //generate randomLines
    var randomLine = Array.fill[Double](L, K, D)(r.nextGaussian())
    var randomVector = Array.fill[Double](K)(r.nextDouble()*W)

    //broadcast frequent used var
    sc.broadcast(randomLine)
    sc.broadcast(randomVector)

    //read in data
    val dfq = sparkSession.read.option("header","false").csv(setQPath)

    val dfn = sparkSession.read.option("header","false").csv(setNPath)

    //convert string to double
    var datasetQ = dfq.rdd.map(r=>(0 until D).map(r.getString(_).toDouble).toList)
    var datasetN = dfn.rdd.map(r=>(0 until D).map(r.getString(_).toDouble).toList)

    //cache the variables
    datasetN.cache()
    datasetQ.cache()

    //normalization the dataset
    var maxn = (0 until D).map(d=>datasetN.map(_(d)).max())
    var minn = (0 until D).map(d=>datasetN.map(_(d)).min())

    //broadcast frequent used variable
    sc.broadcast(maxn)
    sc.broadcast(minn)

    //do normalization
    datasetN = datasetN.map(r=>(0 until D).map(d=> if((maxn(d)-minn(d))>0) (r(d)-minn(d))/(maxn(d)-minn(d)) else 0.5).toList)

    //Q is small set, collect it and then broadcast it
    var datasetQB = datasetQ.collect().toList
    datasetQB = datasetQB.map(r=>(0 until D).map(d=> if((maxn(d)-minn(d))>0) (r(d)-minn(d))/(maxn(d)-minn(d)) else 0.5).toList)

    //broadcast frequent used small and cache reused -- spark practice
    sc.broadcast(datasetQB)
    datasetN.cache()

    //get hash values
	var hashMatrixN = datasetN.map(n=>(0 until L).map(l=>(0 until K).map(k=>math.floor((0 until D).map(d=>n(d)*randomLine(l)(k)(d)).reduce(_+_)/W)*randomVector(k)).reduce(_+_)))

	var hashMatrixQB = datasetQB.map(n=>(0 until L).map(l=>(0 until K).map(k=>math.floor((0 until D).map(d=>n(d)*randomLine(l)(k)(d)).reduce(_+_)/W)*randomVector(k)).reduce(_+_)))

    //still cache...
    hashMatrixN.cache()

    //get the collisionMatrixes
	var collisionMatrix = hashMatrixN.map(n=>hashMatrixQB.map(q=>(0 until L).map(l=>if(n(l)==q(l)) 1 else 0).reduce(_+_)))

    //rdd still cache..
    collisionMatrix.cache()

    //filter out candidate set
    var candidateSet = collisionMatrix.map(h=>(0 until N).filter(n=>(h(n)>T)))

    val result = candidateSet.collect()

    val text = result.map(_.mkString(",")).mkString("\n")

    // write to outputpath
    val file = new File(outputPath)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()


