package sharp.smartplug;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rohit Gurjar on 1/10/2017.
 */

public class ScheduleInfo {

    @SerializedName("periodic_task")
    public List<Periodic_task> periodic_tasks=null;

    @SerializedName("timer_task")
    public List<Timer_task> timer_tasks;

    @SerializedName("random_timer_task")
    public Random_Timer_task random_timer_tasks;

    @SerializedName("cycle_timer_task")
    public Cycle_Timer_task cycle_timer_tasks;

    public List<Periodic_task> getPeriodicTask() {
        return periodic_tasks;
    }

    public void setPeriodicTask(List<Periodic_task> periodicTask) {
        this.periodic_tasks = periodicTask;
    }

    public List<Timer_task> getTimerTask() {
        return timer_tasks;
    }

    public void setTimerTask(List<Timer_task> timerTask) {
        this.timer_tasks = timerTask;
    }

    public Random_Timer_task getRandomTimerTask() {
        return random_timer_tasks;
    }

    public void setRandomTimerTask(Random_Timer_task randomTimerTask) {
        this.random_timer_tasks = randomTimerTask;
    }

    public Cycle_Timer_task getCycleTimerTask() {
        return cycle_timer_tasks;
    }

    public void setCycleTimerTask(Cycle_Timer_task cycleTimerTask) {
        this.cycle_timer_tasks = cycleTimerTask;
    }

    public static class Periodic_task {

        @SerializedName("enable")
        int enable;

        @SerializedName("on_time")
        String on_time;

        @SerializedName("off_time")
        String off_time;

        @SerializedName("repeat")
        int repeat;

        @SerializedName("on_done")
        int on_done;

        @SerializedName("off_done")
        int off_done;

        @SerializedName("mask")
        int mask;

        Periodic_task(int enable,String on_time,String off_time,int repeat,int on_done,int off_done,int mask){

            this.enable = enable;
            this.on_time = on_time;
            this.off_time = off_time;
            this.repeat = repeat;
            this.on_done = on_done;
            this.off_done = off_done;
            this.mask = mask;
        }

        //getters
        public int getEnable() {
            return enable;
        }

        public String getOnTime() {
            return on_time;
        }

        public String getOffTime() {
            return off_time;
        }

        public int getRepeat() {
            return repeat;
        }

        public int getOnDone() {
            return on_done;
        }

        public int getOffDone() {
            return off_done;
        }

        public int getMask() {
            return mask;
        }

        //setters
        public void setEnable(int enable) {
            this.enable = enable;
        }

        public void setOnTime(String on_time) {
            this.on_time = on_time;
        }

        public void setOffTime(String off_time) {
            this.off_time = off_time;
        }

        public void setRepeat(int repeat) {
            this.repeat = repeat;
        }

        public void setOnDone(int on_done) {
            this.on_done = on_done;
        }

        public void setOffDone(int off_done) {
            this.off_done = off_done;
        }

        public void setMask(int mask) {
            this.mask = mask;
        }

    }

    public static class Timer_task {

        @SerializedName("on_enable")
        private int on_enable;

        @SerializedName("on_time")
        private String on_time;

        @SerializedName("off_enable")
        private int off_enable;

        @SerializedName("off_time")
        private String off_time;

        Timer_task(int on_enable,String on_time,int off_enable,String off_time){

            this.on_enable = on_enable;
            this.on_time = on_time;
            this.off_enable = off_enable;
            this.off_time = off_time;
        }

        //getters
        public int getOnEnable(){ return on_enable;}

        public String getOnTime(){ return on_time;}

        public int getOffEnable(){ return off_enable;}

        public String getOffTime(){ return off_time;}

        //setters
        public void setOnEnable(int on_enable){ this.on_enable = on_enable;}

        public void setOnTime(String on_time){ this.on_time = on_time;}

        public void setOffEnable(int off_enable){ this.off_enable = off_enable;}

        public void setOffTime(String off_time){ this.off_time = off_time;}
    }

    public static class Random_Timer_task {

        @SerializedName("start_time")
        private String start_time;

        @SerializedName("end_time")
        private String end_time;

        @SerializedName("repeat")
        private int repeat;

        @SerializedName("enable")
        private int enable;

        Random_Timer_task(String start_time,String end_time,int repeat,int enable){

            this.start_time = start_time;
            this.end_time = end_time;
            this.repeat = repeat;
            this.enable = enable;
        }

        //getters
        public String getStartTime(){ return start_time;}

        public String getEndTime(){ return end_time;}

        public int getRepeat(){ return repeat;}

        public int getEnable(){ return enable;}

        //setters
        public void setStartTime(String start_time){ this.start_time = start_time;}

        public void setEndTime(String end_time){ this.end_time = end_time;}

        public void setRepeat(int repeat){ this.repeat = repeat;}

        public void setEnable(int enable){ this.enable = enable;}
    }

    public static class Cycle_Timer_task {

        @SerializedName("start_time")
        private String start_time;

        @SerializedName("end_time")
        private String end_time;

        @SerializedName("repeat")
        private int repeat;

        @SerializedName("enable")
        private int enable;

        @SerializedName("on_level")
        private int on_level;

        @SerializedName("off_level")
        private int off_level;

        Cycle_Timer_task(String start_time,String end_time,int repeat,int enable,int on_level,int off_level){

            this.start_time = start_time;
            this.end_time = end_time;
            this.repeat = repeat;
            this.enable = enable;
            this.on_level = on_level;
            this.off_level = off_level;
        }

        //getters
        public String getStartTime(){ return start_time;}

        public String getEndTime(){ return end_time;}

        public int getRepeat(){ return repeat;}

        public int getEnable(){ return enable;}

        public int getOnLevel(){ return on_level;}

        public int getOffLevel(){ return off_level;}

        //setters
        public void setStartTime(String start_time){ this.start_time = start_time;}

        public void setEndTime(String end_time){ this.end_time = end_time;}

        public void setRepeat(int repeat){ this.repeat = repeat;}

        public void setEnable(int enable){ this.enable = enable;}

        public void setOnLevel(int on_level){ this.on_level = on_level;}

        public void setOffLevel(int off_level){ this.off_level = off_level;}
    }
}
