package dev.jbang.source;

import static dev.jbang.cli.BaseCommand.EXIT_UNEXPECTED_STATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import dev.jbang.cli.ExitException;
import dev.jbang.util.Util;

/**
 * This class models a source-target relationship where the source is a
 * ResourceRef and the target is a simple Path. This is used for things like the
 * //FILES target=source directives in scripts and templates.
 */
public class RefTarget {
	protected final ResourceRef source;
	protected final Path target;

	protected RefTarget(ResourceRef source, Path target) {
		assert (source != null);
		this.source = source;
		this.target = target;
	}

	public ResourceRef getSource() {
		return source;
	}

	public Path getTarget() {
		return target;
	}

	public Path to(Path parent) {
		Path p = target != null ? target : source.getFile().getFileName();
		return parent.resolve(p);
	}

	public void copy(Path destroot) {
		Path from = source.getFile();
		Path to = to(destroot);
		Util.verboseMsg("Copying " + from + " to " + to);
		try {
			if (!to.toFile().getParentFile().exists()) {
				to.toFile().getParentFile().mkdirs();
			}
			Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new ExitException(EXIT_UNEXPECTED_STATE, "Could not copy " + from + " to " + to, ioe);
		}
	}

	public static RefTarget create(String ref, Path dest, ResourceResolver siblingResolver) {
		return new RefTarget(siblingResolver.resolve(ref), dest);
	}

	public static RefTarget create(ResourceRef ref, Path dest) {
		return new RefTarget(ref, dest);
	}
}
