import java.util.Calendar;
/**
 * Read web server data and analyze hourly access patterns.
 * 
 * @author Omar Malik
 * @version 2023.10.13
 */
public class LogAnalyzer
{
    private int[] hourCounts;
    private LogfileReader reader;
    private int totalAccesses; // Track total accesses
    private Calendar when;

    /**
     * Creates an object to analyze hourly web accesses.
     * 
     * @param logFileName The log file to be analyzed.
     */
    public LogAnalyzer(String logFileName)
    { 
        hourCounts = new int[24];
        reader = new LogfileReader(logFileName);
        totalAccesses = 0; // Initialize total accesses
        when = Calendar.getInstance();
    }

    /**
     * Analyze the hourly access data from the log file.
     */
    public void analyzeHourlyData()
    {
        while(reader.hasNext()) {
            LogEntry entry = reader.next();
            int hour = entry.getHour();
            hourCounts[hour]++;
            totalAccesses++; // Increment total accesses
        }
    }

    /**
     * Prints the hourly counts.
     * These should have been set with a prior
     * call to analyzeHourlyData.
     */
    public void printHourlyCounts()
    {
        System.out.println("Hr: Count");
        for(int hour = 0; hour < hourCounts.length; hour++) {
            System.out.println(hour + ": " + hourCounts[hour]);
        }
    }

    /**
     * Prinst the lines of data read by the LogfileReader
     */
    public void printData()
    {
        reader.printData();
    }

    /**
     * Calculates and returns the total number of accesses.
     *
     * @returns The total number of accesses.
     */
    public int numberOfAccesses()
    {
        return totalAccesses;
    }

    /**
     * Finds the busiest hour.
     * 
     * @returns The busiest hour.
     */
    public int busiestHour()
    {
        int busiestHour = 0;
        int maxAccesses = hourCounts[0];

        for (int hour = 1; hour < hourCounts.length; hour++) {
            if (hourCounts[hour] > maxAccesses) {
                maxAccesses = hourCounts[hour];
                busiestHour = hour;
            }
        }

        return busiestHour;
    }

    /**
     * Finds the quietest hour.
     * 
     * @returns The quietest hour.
     */
    int quietestHour()
    {
        int quietestHour = 0;
        int minAccesses = hourCounts[0];

        for (int hour = 1; hour < hourCounts.length; hour++) {
            if (hourCounts[hour] < minAccesses) {
                minAccesses = hourCounts[hour];
                quietestHour = hour;
            }
        }

        return quietestHour;
    }

    /**
     * Returns the first hour of the busiest two-hour period.
     * 
     * @returns The first hour of the busiest two-hour period.
     */
    public int busiestTwoHour()
    {
        int busiestPeriodFirstHour = 0;
        int maxAccesses = hourCounts[0] + hourCounts[1];

        for (int hour = 1; hour < hourCounts.length - 1; hour++) {
            int periodAccesses = hourCounts[hour] + hourCounts[hour + 1];
            if (periodAccesses > maxAccesses) {
                maxAccesses = periodAccesses;
                busiestPeriodFirstHour = hour;
            }
        }

        return busiestPeriodFirstHour;
    }

    /**
     * Finds the busiest day.
     * 
     * @returns The busiest day as a LogEntry.
     */
    public LogEntry busiestDay()
    {
        LogEntry busiestDay = null;
        int maxAccesses = 0;

        for (int day = 0; day < 31; day++) { // Assuming 31 days in a month
            LogEntry dayEntry = new LogEntry(when.get(Calendar.YEAR), when.get(Calendar.MONTH) + 1, day + 1, 0, 0);
            int accesses = 0;
            for (int hour = 0; hour < 24; hour++) {
                accesses += hourCounts[hour];
            }
            if (accesses > maxAccesses) {
                maxAccesses = accesses;
                busiestDay = dayEntry;
            }
        }

        return busiestDay;
    }

    /**
     * Finds the quietest day.
     * 
     * @returns The quietest day.
     */
    public LogEntry quietestDay()
    {
        LogEntry quietestDay = null;
        int minAccesses = Integer.MAX_VALUE;

        for (int day = 0; day < 30; day++) {
            LogEntry dayEntry = new LogEntry(when.get(Calendar.YEAR), when.get(Calendar.MONTH) + 1, day + 1, 0, 0);
            int accesses = 0;
            for (int hour = 0; hour < 24; hour++) {
                accesses += hourCounts[hour];
            }
            if (accesses < minAccesses) {
                minAccesses = accesses;
                quietestDay = dayEntry;
            }
        }

        return quietestDay;
    }

    /**
     * Calculates and returns total accesses per month.
     * 
     * @returns the total accesses for each month.
     */
    public int[] totalAccessesPerMonth()
    {
        int[] totalAccessesPerMonth = new int[12];
        for (int month = 0; month < 12; month++) {
            for (int day = 0; day < 31; day++) { // Assuming 31 days in a month
                LogEntry dayEntry = new LogEntry(when.get(Calendar.YEAR), month + 1, day + 1, 0, 0);
                int accesses = 0;
                for (int hour = 0; hour < 24; hour++) {
                    accesses += hourCounts[hour];
                }
                totalAccessesPerMonth[month] += accesses;
            }
        }
        return totalAccessesPerMonth;
    }

    /**
     * Finds the busiest month.
     * 
     * @returns the busiest month.
     */
    public LogEntry busiestMonth()
    {
        int[] totalAccessesPerMonth = totalAccessesPerMonth();
        int busiestMonth = 0;
        int maxAccesses = totalAccessesPerMonth[0];

        for (int month = 1; month < totalAccessesPerMonth.length; month++) {
            if (totalAccessesPerMonth[month] > maxAccesses) {
                maxAccesses = totalAccessesPerMonth[month];
                busiestMonth = month;
            }
        }

        return new LogEntry(when.get(Calendar.YEAR), busiestMonth + 1, 1, 0, 0);
    }

    /**
     * Finds the quietest month.
     * 
     * @returns The quietest month.
     */
    public LogEntry quietestMonth()
    {
        int[] totalAccessesPerMonth = totalAccessesPerMonth();
        int quietestMonth = 0;
        int minAccesses = totalAccessesPerMonth[0];

        for (int month = 1; month < totalAccessesPerMonth.length; month++) {
            if (totalAccessesPerMonth[month] < minAccesses) {
                minAccesses = totalAccessesPerMonth[month];
                quietestMonth = month;
            }
        }
        return new LogEntry(when.get(Calendar.YEAR), quietestMonth + 1, 1, 0, 0);
    }

    /**
     * Calculates and returns the average accesses per month.
     * 
     * @retursn the average accesses per month as a double.
     */
    public double averageAccessesPerMonth()
    {
        int[] totalAccessesPerMonth = totalAccessesPerMonth();
        int totalAccesses = 0;
        for (int monthAccesses : totalAccessesPerMonth) {
            totalAccesses += monthAccesses;
        }
        return (double) totalAccesses / totalAccessesPerMonth.length;
    }

    /**
     * The main method to run the log analyzer.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args)
        {
        LogfileCreator creator = new LogfileCreator();
        creator.createFile("weblog.txt", 255);

        LogAnalyzer analyzer = new LogAnalyzer("weblog.txt");
        analyzer.analyzeHourlyData();
        analyzer.printHourlyCounts();
        System.out.println("The total accesses is: " + analyzer.numberOfAccesses());

        int busiestHour = analyzer.busiestHour();
        System.out.println("The busiest hour is: " + busiestHour);

        int quietestHour = analyzer.quietestHour();
        System.out.println("The quietest hour is: " + quietestHour);

        LogEntry busiestDay = analyzer.busiestDay();
        System.out.println("The busiest day is: " + busiestDay);

        LogEntry quietestDay = analyzer.quietestDay();
        System.out.println("The quietest day is: " + quietestDay);

        int[] totalAccessesPerMonth = analyzer.totalAccessesPerMonth();
        System.out.println("Total accesses per month: ");
        for (int i = 0; i < totalAccessesPerMonth.length; i++) {
            System.out.println("Month " + (i + 1) + ": " + totalAccessesPerMonth[i]);
        }

        LogEntry busiestMonth = analyzer.busiestMonth();
        System.out.println("The busiest month is: " + busiestMonth);

        LogEntry quietestMonth = analyzer.quietestMonth();
        System.out.println("The quietest month is: " + quietestMonth);

        double averageAccessesPerMonth = analyzer.averageAccessesPerMonth();
        System.out.println("Average accesses per month: " + averageAccessesPerMonth);
    }
}
