/*
 * Copyright 2019-2021 CloudNetService team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.anweisen.cloud.modules.cloudflare.api.dns;

import net.anweisen.utilities.common.config.Document;

public class DnsRecord {

	protected String type;
	protected String name;
	protected String content;

	protected int ttl;
	protected boolean proxied;

	protected Document data;

	private DnsRecord() {
	}

	public DnsRecord(String type, String name, String content, int ttl, boolean proxied, Document data) {
		this.type = type;
		this.name = name;
		this.content = content;
		this.ttl = ttl;
		this.proxied = proxied;
		this.data = data;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getTtl() {
		return this.ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public boolean isProxied() {
		return this.proxied;
	}

	public void setProxied(boolean proxied) {
		this.proxied = proxied;
	}

	public Document getData() {
		return this.data;
	}

	public void setData(Document data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "DnsRecord[type=" + type + " name=" + name + " content=" + content + " ttl=" + ttl + " proxied=" + proxied + " data=" + data + "]";
	}
}
