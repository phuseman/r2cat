/***************************************************************************
 *   Copyright (C) 2007 by Tobias Marschall                                *
 *                                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package de.bielefeld.uni.cebitec.cav.utils;

/**
 * @author Tobias Marschall
 * 
 */

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.EmptyStackException;
import java.util.Stack;

public final class Timer {
	public enum Level {
		QUIET, NORMAL, VERBOSE, EVERYTHING, INSANE
	};

	private static Timer instance = null;

	private Stack<Long> times;

	private Stack<Long> cpuTimes;

	private boolean timingActive;

	private Level defaultLevel;

	private Level currentLevel;

	private float lastPeriod;

	private float lastPeriodCpu;

	private Timer() {
		defaultLevel = Level.NORMAL;
		currentLevel = Level.QUIET;
		timingActive = false;
		times = null;
		lastPeriod = 0.0f;
		lastPeriodCpu = 0.0f;
		this.setTimingActive(true);
	}

	public static Timer getInstance() {
		if (instance == null) {
			instance = new Timer();

		}
		return instance;
	}

	public void startTimer() {
		if (!timingActive) {
			return;
		}
		times.push(System.currentTimeMillis());
		ThreadMXBean tb = ManagementFactory.getThreadMXBean();
		cpuTimes.push(tb.getCurrentThreadCpuTime());
	}

	public void stopTimer(String message) {
		if (!timingActive) {
			return;
		}
		String s = "";
		try {
			Long t0 = times.pop();
			long t = System.currentTimeMillis() - t0;
			Long t0cput = cpuTimes.pop();
			ThreadMXBean tb = ManagementFactory.getThreadMXBean();
			long tCpu = tb.getCurrentThreadCpuTime() - t0cput;
			lastPeriod = t / 1000.0f;
			lastPeriodCpu = (tCpu) * 1e-9f;
			for (int i = 0; i < times.size(); i++) {
				s += "   ";
			}
			s += String.format("timer: \"%s\" = %.3f / %.3f", message,
					lastPeriod, lastPeriodCpu);
		} catch (EmptyStackException e) {
			s += String.format("timer: \"%s\" = not stored", message);
		}
		System.out.println(s);
	}
	
	public String stopTimer() {
		if (!timingActive) {
			return "";
		}
		String s = "";
		try {
			Long t0 = times.pop();
			long t = System.currentTimeMillis() - t0;
			Long t0cput = cpuTimes.pop();
			ThreadMXBean tb = ManagementFactory.getThreadMXBean();
			long tCpu = tb.getCurrentThreadCpuTime() - t0cput;
			lastPeriod = t / 1000.0f;
			lastPeriodCpu = (tCpu) * 1e-9f;

			s = String.format(" real:%.3f cpu:%.3f s", lastPeriod, lastPeriodCpu);
		} catch (EmptyStackException e) {
			s = "n/a"; 
		}
		return s;
	}

	public void restartTimer(String message) {
		if (!timingActive) {
			return;
		}
		stopTimer(message);
		startTimer();
	}
	
	public String  restartTimer() {
String out="";
		if (!timingActive) {
			return null;
		}
		
		out = stopTimer();
		startTimer();
		
		return out;
	}

	public Level getLogLevel() {
		return currentLevel;
	}

	public void setLogLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}

	public boolean isTimingActive() {
		return timingActive;
	}

	public void setTimingActive(boolean timingActive) {
		this.timingActive = timingActive;
		if (timingActive) {
			times = new Stack<Long>();
			cpuTimes = new Stack<Long>();
		}
	}

	public void print(Level level, String s) {
		if (level.compareTo(currentLevel) <= 0) {
			System.out.println(s);
		}
	}

	public void printNormal(String s) {
		print(Level.NORMAL, s);
	}

	public void printVerbose(String s) {
		print(Level.VERBOSE, s);
	}

	public void printEverything(String s) {
		print(Level.EVERYTHING, s);
	}

	public void printInsane(String s) {
		print(Level.INSANE, s);
	}

	public void print(String s) {
		print(defaultLevel, s);
	}

	public float getLastPeriod() {
		return lastPeriod;
	}

	public float getLastPeriodCpu() {
		return lastPeriodCpu;
	}
}