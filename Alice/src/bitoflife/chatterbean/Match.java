/*
Copyleft (C) 2005 H锟絣io Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean;

import static bitoflife.chatterbean.text.Sentence.ASTERISK;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bitoflife.chatterbean.text.Sentence;

/**
 * Contains information about a match operation, which is needed by the classes
 * of the <code>bitoflife.chatterbean.aiml</code> to produce a proper response.
 */
public class Match implements Serializable { // Math涓轰粈涔堝畾涔変负鍙簭鍒楀寲鍛紵
	/*
	 * Inner Classes
	 */

	public enum Section {
		PATTERN, THAT, TOPIC;
	}

	/*
	 * Attributes
	 */

	/**
	 * Version class identifier for the serialization engine. Matches the number
	 * of the last revision where the class was created / modified.
	 */
	private static final long serialVersionUID = 8L;

	private final Map<Section, List<String>> sections = new HashMap<Section, List<String>>();

	private AliceBot callback;

	private Sentence input;

	private Sentence that;

	private Sentence topic;

	private String[] matchPath;

	// 鍙戠幇杩欓噷鐨勫垪琛ㄩ暱搴﹂兘鏄�鐨勶紝鏄笉鏄鍙敮鎸�涓�閰嶇锛燂紵锛熷垪琛ㄩ噷闈㈠簲璇ユ槸瀛樼殑鏄�閰嶇鎵�尮閰嶇殑鍐呭鍚э紵
	// 浠ｇ爜蹇�浠ｇ爜鍧楃殑浣滅敤锛�	
	{
		sections.put(Section.PATTERN, new ArrayList<String>(2)); // Pattern
																	// wildcards
		sections.put(Section.THAT, new ArrayList<String>(2)); // That wildcards
		sections.put(Section.TOPIC, new ArrayList<String>(2)); // Topic
																// wildcards
	}

	/*
	 * Constructor
	 */

	public Match() {
	}

	public Match(AliceBot callback, Sentence input, Sentence that,
			Sentence topic) {
		this.callback = callback;
		this.input = input;
		this.that = that;
		this.topic = topic;
		setUpMatchPath(input.getSplittedOfSentence(),
				that.getSplittedOfSentence(), topic.getSplittedOfSentence());
	}

	public Match(Sentence input) {
		this(null, input, ASTERISK, ASTERISK);
	}

	/*
	 * Methods
	 */

	private void appendWildcard(List<String> section, Sentence source,
			int beginIndex, int endIndex) {
		if (beginIndex == endIndex) {
			section.add(0, "");
		} else
			try {
				section.add(0, source.original(beginIndex, endIndex)); // 鍘熷杈撳叆瀛楃涓茬殑涓�儴鍒嗐�
			} catch (Exception e) {
				// throw new RuntimeException("Source: {\"" +
				// source.getOriginal() + "\", \"" + source.getNormalized() +
				// "\"}\n" +
				// "Begin Index: " + beginIndex + "\n" +
				// "End Index: " + endIndex, e);
			}
	}

	private void setUpMatchPath(String[] pattern, String[] that, String[] topic) {
		int m = pattern.length, n = that.length, o = topic.length;
		matchPath = new String[m + 1 + n + 1 + o];
		matchPath[m] = "<THAT>";
		matchPath[m + 1 + n] = "<TOPIC>";

		System.arraycopy(pattern, 0, matchPath, 0, m);
		System.arraycopy(that, 0, matchPath, m + 1, n);
		System.arraycopy(topic, 0, matchPath, m + 1 + n + 1, o);
	}

	// 鏍规嵁閮ㄥ垎鐨勯暱搴﹀垎绾у鐞嗐�
	public void appendWildcard(int beginIndex, int endIndex) {
		int inputLength = input.length();
		if (beginIndex <= inputLength) {
			appendWildcard(sections.get(Section.PATTERN), input, beginIndex,
					endIndex);
			return;
		}

		beginIndex = beginIndex - (inputLength + 1);
		endIndex = endIndex - (inputLength + 1);

		int thatLength = that.length();
		if (beginIndex <= thatLength) {
			appendWildcard(sections.get(Section.THAT), that, beginIndex,
					endIndex);
			return;
		}

		beginIndex = beginIndex - (thatLength + 1);
		endIndex = endIndex - (thatLength + 1);

		int topicLength = topic.length();
		if (beginIndex < topicLength)
			appendWildcard(sections.get(Section.TOPIC), topic, beginIndex,
					endIndex);
	}

	/**
	 * Gets the contents for the (index)th wildcard in the matched section.
	 */
	// 浠庤繖閲屽彲浠ョ湅鍑虹▼搴忓彧鑳戒繚瀛�涓�*'鍙风殑鍐呭銆�	
	public String wildcard(Section section, int index) {
		List<String> wildcards = sections.get(section);

		// fixed by lcl
		if (wildcards.size() == 0)// 濡傛灉鏄垜锛屾垜鏄笉浼氭兂鍒板姞杩欎釜淇濋櫓鐨勩�鍥犱负鎴戜細瑙夊緱浠栫殑闀垮害鑲畾灏辨槸2浜嗐�
			return "";
		int i = index - 1;
		if (i < wildcards.size() && i > -1)
			return wildcards.get(i);
		else
			return "";
	}

	/*
	 * Properties
	 */

	public AliceBot getCallback() {
		return callback;
	}

	public void setCallback(AliceBot callback) {
		this.callback = callback;
	}

	public String[] getMatchPath() {
		return matchPath;
	}

	public String getMatchPathByIndex(int index) {
		return matchPath[index];
	}

	public int getMatchPathLength() {
		return matchPath.length;
	}
}
