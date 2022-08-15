/*
 * JarFilter
 * Copyright (C) 2022  Su5eD
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package dev.su5ed.jarfilter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JarFilter {
    
    public static void main(final String[] args) throws IOException {
        final OptionParser parser = new OptionParser();
        final OptionSpec<File> inputOpt = parser.accepts("input", "Input jar file").withRequiredArg().ofType(File.class).required();
        final OptionSpec<File> outputOpt = parser.accepts("output", "Output jar file").withRequiredArg().ofType(File.class).required();
        final OptionSpec<String> filterOpt = parser.accepts("filter", "Comma-separated list of excluded packages").withRequiredArg().ofType(String.class).required();

        final OptionSet options = parser.parse(args);

        final File input = options.valueOf(inputOpt);
        final File output = options.valueOf(outputOpt);
        final List<String> filter = Arrays.asList(options.valueOf(filterOpt).split(","));

        log("JarFilter: ");
        log("  Input:    " + input);
        log("  Output:   " + output);
        log("  Filter:   " + filter);
        
        try(final ZipInputStream zis = new ZipInputStream(new FileInputStream(input));
            final ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(output))) {
            
            for (ZipEntry entry; (entry = zis.getNextEntry()) != null; ) {
                final String name = entry.getName();
                if (filter.stream().anyMatch(name::startsWith)) {
                    log("Excluding: " + name);
                    continue;
                }
                
                zout.putNextEntry(entry);
                copy(zis, zout);
                zout.closeEntry();
            }
        }
    }
    
    public static void copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[8192];
        if (input != null) {
            for (int n; (n = input.read(buffer)) != -1; ) {
                output.write(buffer, 0, n);
            }
        }
    }
    
    private static void log(String message) {
        System.out.println(message);
    }
}
