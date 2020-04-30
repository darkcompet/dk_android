/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
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

package tool.compet.playservice.location.constant;

public interface DkPaidApi$ {
	String URL_PLACES_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?key=%s";
	String URL_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=%s";
	String URL_GEO = "https://maps.googleapis.com/maps/api/geocode/json?key=%s";
	String URL_PLACES = "https://maps.googleapis.com/maps/api/place/search/json?key=%s";
	String URL_PLACES_TEXT = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=%s";
	String URL_PLACES_AUTOCOMPLETE = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=%s";
	String URL_STATIC_MAP = "https://maps.googleapis.com/maps/api/staticmap?key=%s";
	String URL_ALTITUDE = "https://maps.googleapis.com/maps/api/elevation/json?key=%s";
}
