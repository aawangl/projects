from pyspark import SparkContext
import Sliding, argparse

def map_children(value):
    #appends the children of the board state to the RDD tuple
    #value is a boardstate
    boardList = [];
    boardList.append(value)
    if value[1] == level:    
        childrenList = Sliding.children(WIDTH, HEIGHT, value[0]) 
        for child in childrenList:
            boardList.append((child, value[1]+1))
    
    return boardList

def get_recent(value):
    #obtains the most recent pairs in an RDD. Namely the configurations with 
    #level == level + 1
    if (value[1] == level + 1):
        return True
    

def reduce_children(arg1, arg2):
    # gets rid of duplicate children by keeping the one with the smaller level
    return min(arg1, arg2)

def solve_sliding_puzzle(master, output, height, width):
    """
    Solves a sliding puzzle of the provided height and width.
     master: specifies master url for the spark context
     output: function that accepts string to write to the output file
     height: height of puzzle
     width: width of puzzle
    """
    # Set up the spark context. Use this to create your RDD
    sc = SparkContext(master, "python")

    # Global constants that will be shared across all map and reduce instances.
    # You can also reference these in any helper functions you write.
    global HEIGHT, WIDTH, level

    # Initialize global constants
    HEIGHT=height
    WIDTH=width
    level = 0 # this "constant" will change, but it remains constant for every MapReduce job

    # The solution configuration for this sliding puzzle. You will begin exploring the tree from this node
    sol = Sliding.solution(WIDTH, HEIGHT)

    #put this in a tuple and parallelize it, now flatMap it with [original, flatmap(original)]
    # get all the children by using Sliding.children(W,H,sol)
    #
    
    """ YOUR MAP REDUCE PROCESSING CODE HERE """
    #initialize RDD
    data = [(sol, level)]
    distdata = sc.parallelize(data)
    #keys are (A,B,-,C), values are level
    notDone = True #sets up the loop condition
    while notDone:
        if (level) % 32 == 0:
            distdata = distdata.partitionBy(64) #partitions the RDD 
        distdata = distdata.flatMap(map_children) #adds all the children to the RDD
        distdata = distdata.reduceByKey(reduce_children) #removes duplicate board configurations
        #checks if the loop should be terminated every 8 iterations by looking for new elements
        #no new elements means that the loop is done and notDone variable will be flipped
        if level % 8 == 0: 
            temp = distdata.filter(get_recent)
            if temp.count() == 0:
                notDone = False

        level = level + 1
        
    """ YOUR OUTPUT CODE HERE """
    #collects and sorts the output
    out = distdata.collect()
    temp = []
    for s in out:
        temp.append(str(s[1]) + " " + str(s[0]))
    temp.sort()
    for t in temp:
        output(str(t))
    sc.stop()



""" DO NOT EDIT PAST THIS LINE

You are welcome to read through the following code, but you
do not need to worry about understanding it.
"""

def main():
    """
    Parses command line arguments and runs the solver appropriately.
    If nothing is passed in, the default values are used.
    """
    parser = argparse.ArgumentParser(
            description="Returns back the entire solution graph.")
    parser.add_argument("-M", "--master", type=str, default="local[8]",
            help="url of the master for this job")
    parser.add_argument("-O", "--output", type=str, default="solution-out",
            help="name of the output file")
    parser.add_argument("-H", "--height", type=int, default=2,
            help="height of the puzzle")
    parser.add_argument("-W", "--width", type=int, default=2,
            help="width of the puzzle")
    args = parser.parse_args()


    # open file for writing and create a writer function
    output_file = open(args.output, "w")
    writer = lambda line: output_file.write(line + "\n")

    # call the puzzle solver
    solve_sliding_puzzle(args.master, writer, args.height, args.width)

    # close the output file
    output_file.close()

# begin execution if we are running this file directly
if __name__ == "__main__":
    main()
