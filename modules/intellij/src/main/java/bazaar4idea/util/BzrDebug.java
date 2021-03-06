/*
 * Copyright (c) 2009 Patrick Woodworth
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package bazaar4idea.util;

/**
 * @author Patrick Woodworth
 */
public class BzrDebug {

  public static final boolean EXPERIMENTAL_ENABLED = Boolean.getBoolean("bzr4intellij.enable_experimental_features");

  public static final boolean ROOT_REMAPPING_ENABLED = true; // Boolean.getBoolean("bzr4intellij.enable_root_remapping");
}
