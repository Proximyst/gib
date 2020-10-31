//
// gib - A simple duplication plugin for Bukkit servers.
// Copyright (C) Mariell Hoversholm 2020
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package com.proximyst.gib.utils;

import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ReflectionUtils {
  private ReflectionUtils() throws IllegalAccessException {
    throw new IllegalAccessException(getClass().getSimpleName() + " cannot be instantiated.");
  }

  public static boolean isMethodOnClass(final @NonNull Class<?> clazz, final @NonNull String methodName) {
    // We use #getMethods instead of #getMethod as I don't want to specify types.
    for (final Method method : clazz.getMethods()) {
      if (methodName.equals(method.getName())) {
        return true;
      }
    }

    return false;
  }
}
