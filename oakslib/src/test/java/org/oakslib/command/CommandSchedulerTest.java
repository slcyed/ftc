package org.oakslib.command;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** No-robot JUnit tests for the command/subsystem scheduler. Run via ./gradlew :oakslib:testDebugUnitTest */
public class CommandSchedulerTest {

    /** A command that counts lifecycle calls and optionally finishes after a number of executes. */
    private static class TestCommand extends Command {
        final int finishAfter; // -1 = never finishes on its own (a proper default command)
        int init, exec, end;
        boolean interrupted;

        TestCommand(int finishAfter, Subsystem... requirements) {
            this.finishAfter = finishAfter;
            addRequirements(requirements);
        }

        @Override
        public void initialize() {
            init++;
        }

        @Override
        public void execute() {
            exec++;
        }

        @Override
        public void end(boolean interrupted) {
            end++;
            this.interrupted = interrupted;
        }

        @Override
        public boolean isFinished() {
            return finishAfter != -1 && exec >= finishAfter;
        }
    }

    private CommandScheduler scheduler;

    @Before
    public void setUp() {
        // The scheduler is a singleton; fresh state per test so tests don't interfere.
        scheduler = CommandScheduler.getInstance();
        scheduler.reset();
    }

    @Test
    public void defaultCommandRunsWhenNothingElseRequiresTheSubsystem() {
        Subsystem shooter = new Subsystem() {};
        TestCommand idle = new TestCommand(-1, shooter);
        scheduler.setDefaultCommand(shooter, idle);

        scheduler.run();
        scheduler.run();

        assertEquals(1, idle.init);
        assertTrue(idle.exec >= 1);
        assertEquals(0, idle.end);
    }

    @Test
    public void conflictingCommandInterruptsTheDefaultCommand() {
        Subsystem shooter = new Subsystem() {};
        TestCommand idle = new TestCommand(-1, shooter);
        scheduler.setDefaultCommand(shooter, idle);
        scheduler.run();

        TestCommand shoot = new TestCommand(2, shooter);
        scheduler.schedule(shoot);
        scheduler.run();

        assertEquals(1, idle.end);
        assertTrue(idle.interrupted);
        assertEquals(1, shoot.init);
        assertEquals(1, shoot.exec);
        assertEquals(shoot, scheduler.requiring(shooter));
    }

    @Test
    public void defaultCommandResumesWhenTheConflictingCommandFinishes() {
        Subsystem shooter = new Subsystem() {};
        TestCommand idle = new TestCommand(-1, shooter);
        scheduler.setDefaultCommand(shooter, idle);
        scheduler.run();

        TestCommand shoot = new TestCommand(2, shooter);
        scheduler.schedule(shoot);
        scheduler.run();
        scheduler.run(); // shoot finishes here

        assertFalse(shoot.interrupted);
        assertEquals(1, shoot.end);
        assertEquals(2, idle.init); // initial + resumed
        assertEquals(1, idle.end);
    }

    @Test
    public void schedulingACommandInterruptsTheCommandCurrentlyUsingItsRequirements() {
        Subsystem shooter = new Subsystem() {};
        TestCommand first = new TestCommand(-1, shooter);
        scheduler.schedule(first);
        scheduler.run();

        TestCommand second = new TestCommand(-1, shooter);
        scheduler.schedule(second);
        scheduler.run();

        assertTrue(first.interrupted);
        assertEquals(1, first.end);
        assertEquals(second, scheduler.requiring(shooter));
        assertEquals(second, shooter.getCurrentCommand());
    }

    @Test
    public void cancelCallsEndWithInterruptedAndFreesTheSubsystemForTheDefaultCommand() {
        Subsystem shooter = new Subsystem() {};
        TestCommand idle = new TestCommand(-1, shooter);
        scheduler.setDefaultCommand(shooter, idle);
        scheduler.run();

        TestCommand spin = new TestCommand(-1, shooter);
        scheduler.schedule(spin);
        scheduler.run();

        scheduler.cancel(spin);

        assertEquals(1, spin.end);
        assertTrue(spin.interrupted);
        assertNull(scheduler.requiring(shooter));

        scheduler.run();
        assertEquals(2, idle.init); // resumed after the subsystem was freed
    }

    @Test
    public void triggerScheduledCommandsRunManyLoopsWithoutConcurrentModification() {
        // Regression test: scheduling from triggers while run() is executing used to modify
        // the deferred-schedule set during iteration and throw ConcurrentModificationException.
        final int[] executeCount = {0};
        Command heldA = new Command() {
            @Override
            public void execute() {
                executeCount[0]++;
            }
        };
        Command heldB = new Command() {
            @Override
            public void execute() {
                executeCount[0]++;
            }
        };
        new Trigger(() -> true).whileTrue(heldA);
        new Trigger(() -> true).whileTrue(heldB);

        for (int i = 0; i < 100; i++) {
            scheduler.run();
        }

        assertEquals(200, executeCount[0]);
        assertTrue(scheduler.isScheduled(heldA));
        assertTrue(scheduler.isScheduled(heldB));
    }

    @Test
    public void finishedCommandsRemovedSafelyWhileOthersAreRunning() {
        // Regression test: removing a finished command by iterating over the running-command set
        // used to throw ConcurrentModificationException whenever more commands remained after it.
        Subsystem shooter = new Subsystem() {};
        TestCommand finisher = new TestCommand(1, shooter);
        Command runner = new Command() {};
        scheduler.schedule(finisher);
        scheduler.schedule(runner);

        scheduler.run();

        assertEquals(1, finisher.end);
        assertFalse(finisher.interrupted);
        assertFalse(scheduler.isScheduled(finisher));
        assertTrue(scheduler.isScheduled(runner));
    }
}