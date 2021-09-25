package Sorting;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Random;

public class Main {

    //these values are used to change the overall functioning of the program
    private static final int maxNumber = 5;               //max number to be used for each item
    private static final int minNumber = 1;               //min number to be used for each item

    private static final int trials = 10;                 //the number of trials that will be done to help determine the average time

    private static final int maxN = 2100000;              //determines the max size the lists of size N will go to
    private static final int StartN = 4;                  //the size of the list that will be the beginning point of the trials

    private static final int maxK = 48;                    //determines the max size of the key-values (Strings) to sort
    //private static final int kBeingUsed = 4;              //used to help isolate the values of k being used if not all will be used. 1 = 6; 2 = 12; 3 = 24; 4 = 48
    private static final int StartK = 6;                  //determines the starting value of K to be used for the string size

    private static final int FinalSelection = 65536;      //used to end the selection sort early due to time constraints

    //Sample will hold the list of lists while copy is used to exchange rows in the list
    private static final int[][] Sample = new int[maxN][maxK];
    private static final int[] CopyOfSample = new int[maxK];

    private static final int[][] ArrayForMergesort = new int[maxN][maxK];

    //used to fill with random values
    private static final Random random = new Random();

    public static void main(String[] args) {

        //length of list to be sorted
        long N;

        //the size of the key-values (strings) to sort
        int k;

        //for time and doubling ratios
        long timeStampBefore;
        long timeStampAfter;
        long[] AverageTime = new long[4];
        long[] HoldTimes = new long[4];
        int TimeSlot = 0;

        //for checking if the lists are sorted correctly
        int[] isSortedCorrectly = new int[4];

        //counter
        int i;

        // will be used to determine overall trial number
        int CurrentTrial = 0;

        //to get track of k time value placement within the array
        int k6 = 0;
        int k12 = 1;
        int k24 = 2;
        int k48 = 3;

        // used to switch which sort type will be used in current test
        //0: Selection Sort    1: Quick Sort    2: Merge Sort
        int UseSort = 0;

        //The total number of sorts used in the current test
        int SortTypes = 2;

        //This outer loop is responsible for changing the sort type
        while (UseSort <= SortTypes) {

            //Set N to base value
            N = StartN;

            //reset sorted values to default value of 2 for each sort
            for(i = 0; i < 4; i++)
                isSortedCorrectly[i] = 2;

            //Clean up and break apart the charts with labels to show which sort is being used
            switch (UseSort) {
                case 0:
                    System.out.println("\nSelection Sort:");
                    break;
                case 1:
                    System.out.println("\n\nQuick Sort:");
                    break;
                case 2:
                    System.out.println("\n\nMerge Sort:");
                    break;
            }

            //This loop will double the overall size of the list until a max value is reached or passed
            while (N <= maxN) {

                //set k as base value
                k = StartK;

                //used to shorten time of selection sort if desired to shorten the wait time due to selection sort being n^2
                if(UseSort == 0 && N == FinalSelection)
                    break;

                //Used to switch between k's 6,12,24, and 48 values
                while (k <= maxK) {

                    //begin timed trials
                    while (CurrentTrial < trials) {

                        //generate a new list of Size N using K (size of String) per trial
                        GenerateList(N, k);

                        //obtain a single timed trial, UseTheSort function determines which sort will be used
                        timeStampBefore = getCpuTime();
                        UseTheSort(UseSort, N, k);
                        timeStampAfter = getCpuTime();

                        //will set the default value to 1. 2 is used to determine if k value was not attempted (used when printing results)
                        if(isSortedCorrectly[TimeSlot] == 2)
                            isSortedCorrectly[TimeSlot] = 1;

                        //if trial is sorted correctly, will multiply 1 * 1 and leave a 1
                        //if trial was not sorted correctly, multiply by 0 and will remain 0 throughout the trials
                        isSortedCorrectly[TimeSlot] = isSortedCorrectly[TimeSlot] * isSorted(N,k);

                        //Add the time for each trial
                        AverageTime[TimeSlot] = AverageTime[TimeSlot] + (timeStampAfter - timeStampBefore);

                        // go to the next trial
                        CurrentTrial++;
                    }

                    //calculate the average using the overall number obtained on line 122 and the number of trials
                    AverageTime[TimeSlot] = AverageTime[TimeSlot] / trials;

                    //restore the list to all zeros
                    cleanLists(N);

                    //reset trials, begin next slot, and double k value (list depth)
                    CurrentTrial = 0;
                    TimeSlot++;
                    k = k * 2;
                }

                //If first N in the list
                if (N == StartN) {
                    //Print a header for the chart
                    System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%n",
                            "K = " + StartK,
                            "",
                            "",
                            "",
                            "",
                            "K = " + StartK * 2,
                            "",
                            "",
                            "",
                            "",
                            "K = " + StartK * 2 * 2,
                            "",
                            "",
                            "",
                            "",
                            "K = " + StartK * 2 * 2 * 2,
                            "",
                            "",
                            "",
                            "");

                    System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%n",
                            "N",
                            "Time Average:",
                            "Doubling Ratio",
                            "Expected Ratio",
                            "Sorted?",
                            "N",
                            "Time Average",
                            "Doubling Ratio",
                            "Expected Ratio",
                            "Sorted?",
                            "N",
                            "Time Average",
                            "Doubling Ratio",
                            "Expected Ratio",
                            "Sorted?",
                            "N",
                            "Time Average",
                            "Doubling Ratio",
                            "Expected Ratio",
                            "Sorted?");

                    //print the first row of values using "n/a" for doubling ratios
                    System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%n",
                            N,
                            AverageTime[k6],
                            "N/a",
                            "N/a",
                            printSortedString(isSortedCorrectly[k6]),
                            N,
                            AverageTime[k12],
                            "N/a",
                            "N/a",
                            printSortedString(isSortedCorrectly[k12]),
                            N,
                            AverageTime[k24],
                            "N/a",
                            "N/a",
                            printSortedString(isSortedCorrectly[k24]),
                            N,
                            AverageTime[k48],
                            "N/a",
                            "N/a",
                            printSortedString(isSortedCorrectly[k48]));

                    //Grab and hold the average values to use when finding the doubling ratio
                    for (k = 0; k < 4; k++)
                        HoldTimes[k] = AverageTime[k];
                }
                //on every other iteration after the first N:
                else {
                    //print out the desired information on the rest of the chart of the first N
                    System.out.printf("%20s%20s%20.2f%20s%20s%20s%20s%20.2f%20s%20s%20s%20s%20.02f%20s%20s%20s%20s%20.02f%20s%20s%n",
                            N,
                            AverageTime[k6],
                            (float) AverageTime[k6] / HoldTimes[k6],
                            printExpectedRatio(UseSort, N),
                            printSortedString(isSortedCorrectly[k6]),
                            N,
                            AverageTime[k12],
                            (float) AverageTime[k12] / HoldTimes[k12],
                            printExpectedRatio(UseSort, N),
                            printSortedString(isSortedCorrectly[k12]),
                            N,
                            AverageTime[k24],
                            (float) AverageTime[k24] / HoldTimes[k24],
                            printExpectedRatio(UseSort, N),
                            printSortedString(isSortedCorrectly[k24]),
                            N,
                            AverageTime[k48],
                            (float) AverageTime[k48] / HoldTimes[k48],
                            printExpectedRatio(UseSort, N),
                            printSortedString(isSortedCorrectly[k48]));
                }
                //Grab and hold the average values to use when finding the doubling ratio
                for (i = 0; i < 4; i++)
                    HoldTimes[i] = AverageTime[i];

                //double the list size
                N = N * 2;

                TimeSlot = 0;
            }
            //increment and go to the next sort
            UseSort++;
        }
    }


    /*** Table of contents
     isSorted()
     UseTheSort()
     GenerateLists()
     exchange()
     cleanLists()
     PrintLists()
     SelectionSort()
     quicksort()
     Partition()
     mergesortBegin()
     mergesort()
     merge()
     mergeExchange()
     reverseMergeExchange()
     printListsForMerge()
     printSortedString()
     printExpectedRatio()
     getCpuTime()
     */


    //check if the main array is sorted
    private static int isSorted(long N, int k)
    {
        int i = 0;
        int j = 0;

        while(i < N - 1)
        {
            //walk down the members if they contain the same value
            while(Sample[i][j] == Sample[i+1][j] && j < k - 1)
                j++;

            //if the current value is less then the next value, continue on with the check
            if(Sample[i][j] <= Sample[i+1][j] ) {
                i++;
                j = 0;
            }
            //else return as an error
            else {
                return 0;
            }
        }

        //if the program made it this far then the program is sorted successfully so return a 1
        return 1;
    }


    //set up and prepare which sort is about to be used
    private static void UseTheSort(int UseSort, long N, int k)
    {
        //Switch statement will determine which sort that is used
        switch(UseSort)
        {
            case 0:
                SelectionSort(N, k);
                break;
            case 1:
                quicksort(k, 0, (int)N - 1);
                break;
            case 2:
                mergesortBegin((int)N, k);
                break;
        }
    }


    //generate a random list in preparation for sorting
    static void GenerateList(long listSize, long itemSize)
    {
        int i;
        int j;
        int offsetToPreventZero = 1;

        //go through the list and create a random list of lists
        for (i = 0; i < listSize; i++)
        {
            for (j = 0; j < itemSize; j++)
            {
                Sample[i][j] = offsetToPreventZero + random.nextInt(maxNumber - minNumber);
            }
        }
    }


    //exchange rows i and j
    private static void exchange(long itemSize, int i, int j)
    {
        int c;

        //Exchange two rows in the list
        for (c = 0; c < itemSize; c++)
        {
            CopyOfSample[c] = Sample[i][c];
            Sample[i][c] = Sample[j][c];
            Sample[j][c] = CopyOfSample[c];
        }
    }


    //clean out the main array
    static void cleanLists(long listSize)
    {
        int i;
        int j;
        char reset = 0;

        //clean the main array and set all values to 0
        for (i = 0; i < listSize; i++)
        {
            for (j = 0; j < maxK; j++)
            {
                Sample[i][j] = reset;
            }
        }
    }


    //if needed for troubleshooting, is used to print out the main array
    static void PrintLists(long listSize)
    {
        for (int i = 0; i < listSize; i++) {
            System.out.println(i + ": " + Arrays.toString(Sample[i]));
        }
    }


    //perform the selection sort
    static void SelectionSort(long listSize, long itemSize)
    {
        int i;
        int j;
        int k = 0;

        int lowestValue;
        int lowestPlace;

        while(k < listSize)
        {
            //k will determine how many of the elements are already sorted
            //lowest place and value will determine the lowest value detected as well as the row it sits in.
            lowestValue = Sample[k][0];
            lowestPlace = k;

            //obtain the lowest row
            for (i = k + 1; i < listSize; i++)
            {
                //looks at the first column of each row to see if it is smaller then the current stored value
                if (Sample[i][0] < lowestValue)
                {
                    lowestValue = Sample[i][0];
                    lowestPlace = i;
                }

                //If a value is the same as the current lowest, traverse down both lists until one emerges with the smallest number
                if (Sample[i][0] == lowestValue)
                {
                    j = 0;

                    //traverse down the list until the numbers are not equivalent
                    while (Sample[i][j] == Sample[lowestPlace][j] && j < maxK - 1)
                        j++;

                    //when arriving at the lowest digit, set it as the overall lowest
                    if (Sample[i][j] < Sample[lowestPlace][j])
                    {
                        lowestValue = Sample[i][0];
                        lowestPlace = i;
                    }
                }
            }

            //exchange the selected item with the current spot needed in the list
            exchange(itemSize, k, lowestPlace);

            //increment k so say that the current row is in its final position
            k++;
        }
    }


    //perform the recursive call
    static void quicksort(long itemSize, int low, int high)
    {
        //return right away if list is either empty or has only one item
        if (high <= low)
            return;

        //run the partition to determine the index (which will be at its final position)
        int index = Partition(itemSize, low, high);

        //sort the left side of the list recursively
        quicksort(itemSize, low, index - 1);

        //sort the right side of the list recursively
        quicksort(itemSize, index + 1, high);
    }


    //perform the quicksort operation
    private static int Partition(long itemSize, int low, int high)
    {
        int i = low - 1;
        int j;

        int listItemForJ;

        //pick the final item in the list and make it into a pivot, save location as pivotPoint
        int pivotPoint = high;

        //start j at the bottom of the current list and iterate through each item
        for (j = low; j < pivotPoint; j++) {

            //reset the list item so that every iteration begins comparisons at the beginning of the item
            listItemForJ = 0;

            //if j is smaller then or equal to the pivot point
            if (Sample[j][listItemForJ] <= Sample[pivotPoint][listItemForJ]) {

                //move past all of the same items between j and the pivot if any
                while(Sample[j][listItemForJ] == Sample[pivotPoint][listItemForJ] && listItemForJ < itemSize - 1)
                    listItemForJ++;

                //if j still remains as the lowest item
                if(Sample[j][listItemForJ] < Sample[pivotPoint][listItemForJ]) {

                    i++;

                    //if i and j arent equal, then i is in the right position for j so exchange it
                    if (i != j) {
                        exchange(itemSize, i, j);
                    }
                }
            }
        }

        //increment i if it is less then the pivot point
        if (i < pivotPoint)
            i++;

        //i will be at the correct spot for the pivot, so exchange with pivot
        exchange(itemSize, pivotPoint, i);

        //return i since the value will be in its final spot
        return i;
    }


    //set up and begin the mergesort
    public static void mergesortBegin(int listSize, int itemSize)
    {
        //begin the call here
        mergesort(0, listSize - 1, listSize, itemSize);
    }


    //this one is the recursive call for mergesort
    public static void mergesort(int low, int high, int listSize, int itemSize)
    {
        //will return if theres only a single element
        if(low >= high)
            return;

        //get the middle element
        int mid = (low + high) / 2;

        //split off the lower half
        mergesort(low, mid, listSize, itemSize);

        //split off hte top half
        mergesort(mid + 1, high, listSize, itemSize);

        //merge the two arrays
        merge(low, high, itemSize);
    }


    //perform the merge operation
    public static void merge(int low, int high, int itemSize)
    {
        //get end of first list and beginning of second list
        int endOfLowList = (low + high) / 2;
        int beginSecondList = endOfLowList + 1;

        //get the start of both lists
        int leftArrayStart = low;
        int rightArrayStart = beginSecondList;

        //current index determines which value on the tmp array
        int currentIndex = low;

        //compareSlot is used to walk down the list of k values
        int compareSlot;

        //while(leftArrayStart <= endOfLowList && rightArrayStart <= high)
        while(currentIndex <= high)
        {
            compareSlot = 0;

            //walk down the list until the values are not the same
            while(Sample[leftArrayStart][compareSlot] == Sample[rightArrayStart][compareSlot] && compareSlot < itemSize - 1)
                compareSlot++;

            //if left list value is smaller than right list value & left list value is not empty
            if (Sample[leftArrayStart][compareSlot] <= Sample[rightArrayStart][compareSlot] && Sample[leftArrayStart][compareSlot] != 0)
            {
                //push the left array value to the temp array
                mergeExchange(itemSize, leftArrayStart, currentIndex);

                //if left list is not at the end, go to the next element
                if(leftArrayStart != endOfLowList)
                    leftArrayStart++;
            }
            //if right list value is smaller than left list value & right list value is not empty
            else if (Sample[rightArrayStart][compareSlot] < Sample[leftArrayStart][compareSlot] && Sample[rightArrayStart][compareSlot] != 0)
            {
                //push the right array value to temp array
                mergeExchange(itemSize, rightArrayStart, currentIndex);

                //if right list is not at the end of the list increment to the next value
                if(rightArrayStart != high)
                    rightArrayStart++;
            }
            //if left array value is 0 and right array still has elements
            else if (Sample[leftArrayStart][compareSlot] == 0 && Sample[rightArrayStart][compareSlot] != 0)
            {
                //push the right array to the temp array
                mergeExchange(itemSize, rightArrayStart, currentIndex);

                //go to the next element in the right list if it is not the final element
                if(rightArrayStart != high)
                    rightArrayStart++;
            }
            //if right list is empty and left list still has values
            else if(Sample[rightArrayStart][compareSlot] == 0 && Sample[leftArrayStart][compareSlot] != 0)
            {
                //push left array onto the temp array
                mergeExchange(itemSize, leftArrayStart, currentIndex);

                //if left list is not empty, go to the next element
                if (leftArrayStart != endOfLowList)
                    leftArrayStart++;
            }

            //increment the temp array
            currentIndex++;
        }

        //push all the values from the temp array back into the main array so recursion can grab values
        for(int i = low; i <= high; i++)
        {
            reverseMergeExchange(itemSize, i, i );
        }
    }


    //merge items into the backup array
    private static void mergeExchange(long itemSize, int mainArrayIndex, int tmpArrayIndex)
    {
        int c;

        //push the values from the main array to the temp array then change all the values in the main array to 0 to signal it has been used
        for (c = 0; c < itemSize; c++)
        {
            ArrayForMergesort[tmpArrayIndex][c] = Sample[mainArrayIndex][c];
            Sample[mainArrayIndex][c] =  CopyOfSample[c];
        }
    }


    //merge items back into the main array
    private static void reverseMergeExchange(long itemSize, int mainArrayIndex, int tmpArrayIndex)
    {
        int c;

        //Push all the values back into the main array and empty the temp array
        for (c = 0; c < itemSize; c++)
        {
            Sample[mainArrayIndex][c] = ArrayForMergesort[tmpArrayIndex][c];
            ArrayForMergesort[tmpArrayIndex][c] =  CopyOfSample[c];
        }
    }


    //if needed, used to help picture the merge sort
    static void printListsForMerge(int low, long listSize)
    {
        if(listSize != maxN) {
            while (low <= listSize) {
                System.out.println(low + ": " + Arrays.toString(ArrayForMergesort[low]));
                low++;
            }
        }
        else {
            while (low < listSize) {
                System.out.println(low + ": " + Arrays.toString(ArrayForMergesort[low]));
                low++;

            }
        }
        System.out.println("\n");
    }


    //this is used by the section that prints the results to tell if the list has been sorted correctly
    static String printSortedString(int isSorted)
    {
        if(isSorted == 2)
            return "Not Attempted";
        else if(isSorted == 1)
            return "Yes";
        else
            return "No";
    }


    //calculates the expected ratio
    static String printExpectedRatio(int UseSort, long N)
    {
        //Switch statement will determine which sort that is used
        switch(UseSort)
        {
            case 0:
                return "4.0";
            case 1:
//                return String.valueOf((N * Math.log(N)) / ((N/2) * Math.log(N/2)));
            case 2:
                return String.valueOf((N * Math.log(N)) / ((N/2) * Math.log(N/2)));
            case 3:
                return "?????";
        }
        return "No Sort Used";
    }


/** Get CPU time in nanoseconds since the program(thread) started. **/
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
    public static long getCpuTime( )
    {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;
    }
}



