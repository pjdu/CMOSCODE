package cn.com.chinaccs.datasite.main.gps;

import java.io.Serializable;

public class GpsSatellite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6824166055224783662L;
	// 卫星的方位角，浮点型数据
	private float azimuth;
	// 卫星的高度，浮点型数据
	private float elevation;
	// 卫星的伪随机噪声码，整形数据
	private int prn;
	// 卫星的信噪比，浮点型数据
	private float snr;
	// 卫星是否有年历表，布尔型数据
	private boolean almanac;
	// 卫星是否有星历表，布尔型数据
	private boolean ephemeris;
	// 是否已用于定位
	private boolean isUse;

	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = elevation;
	}

	public int getPrn() {
		return prn;
	}

	public void setPrn(int prn) {
		this.prn = prn;
	}

	public float getSnr() {
		return snr;
	}

	public void setSnr(float snr) {
		this.snr = snr;
	}

	public boolean isAlmanac() {
		return almanac;
	}

	public void setAlmanac(boolean almanac) {
		this.almanac = almanac;
	}

	public boolean isEphemeris() {
		return ephemeris;
	}

	public void setEphemeris(boolean ephemeris) {
		this.ephemeris = ephemeris;
	}

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
}
