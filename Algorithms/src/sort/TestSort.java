package sort;

public class TestSort
{
    private static final int NUM_ITEMS = 1000;
    private static int theSeed = 1;
    
    /**
     * Randomly rearrange an array.
     * The random numbers used depend on the time and day.
     * @param a the array.
     */
    public static final <AnyType> void permute( AnyType [ ] a )
    {
        Random r = new Random( );

        for( int j = 1; j < a.length; j++ )
            SortUtil.swapReferences( a, j, r.nextInt( 0, j ) );
    }

    public static void checkSort( Integer [ ] a )
    {
        for( int i = 0; i < a.length; i++ )
            if( a[ i ] != i )
                System.out.println( "Error at " + i );
        System.out.println( "Finished checksort" );
    }


    public static void main( String [ ] args )
    {
        Integer [ ] a = new Integer[ NUM_ITEMS ];
        for( int i = 0; i < a.length; i++ )
            a[ i ] = i;

        for( theSeed = 0; theSeed < 20; theSeed++ )
        {
            permute( a );
            SortUtil.insertionSort( a );
            checkSort( a );

            permute( a );
            SortUtil.heapsort( a );
            checkSort( a );

            permute( a );
            SortUtil.shellsort( a );
            checkSort( a );

            permute( a );
            SortUtil.mergeSort( a );
            checkSort( a );

            permute( a );
            SortUtil.quicksort( a );
            checkSort( a );

            permute( a );
            SortUtil.quickSelect( a, NUM_ITEMS / 2 );
            System.out.println( a[ NUM_ITEMS / 2 - 1 ] + " is " + NUM_ITEMS / 2 +"th smallest" );
        }
    }
}

