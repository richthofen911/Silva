package net.callofdroidy.silva;

/**
 * Created by admin on 08/04/16.
 */
public class SmsGenerator {

    private static SmsGenerator ourInstance = new SmsGenerator();

    public static SmsGenerator getInstance() {
        return ourInstance;
    }

    private SmsGenerator() {
    }

    public void smsContentAnalyzer(String input){
        boolean leaveOrNot = false;
        boolean isOnTheWay = false;
        String timeEstimatedToLeave = "unknown";
        String timeEstimatedToArrive = "unknown";


        if(input.contains(Constants.SMS_KEYWORD_HOW_LONG_1) && input.contains(Constants.SMS_KEYWORD_ARRIVE)){
            if(isOnTheWay)
                timeEstimatedToArrive = String.valueOf(estimateTimeToArrive());
                // then reply a message about how long to arrive
            else{
                // reply not leave yet
            }
        }else if((input.contains(Constants.SMS_KEYWORD_LEAVE) && !input.contains(Constants.SMS_KEYWORD_WHEN)) ||
                (input.contains(Constants.SMS_KEYWORD_WHEN) && input.contains(Constants.SMS_KEYWORD_LEAVE))){
            if(estimatedTimeToLeave() < 3){
                // reply yes
            }else{
                timeEstimatedToLeave = String.valueOf(estimatedTimeToLeave());
            }
        }


    }

    private int estimateTimeToArrive(){
        int timeEstimated = 10;
        return timeEstimated;
    }

    private int estimatedTimeToLeave(){
        int timeEstimated = 10;
        return timeEstimated;
    }
}
