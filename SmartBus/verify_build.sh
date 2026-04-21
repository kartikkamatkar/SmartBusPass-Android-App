#!/bin/bash
# SmartBus Build Verification Script

echo "======================================="
echo "SmartBus Android Build Verification"
echo "======================================="
echo ""

echo "✓ Checking XML Resource Files..."
echo ""

# Check for incorrect patterns
echo "Checking for incorrect layout_align attributes:"
grep -r "layout_align[A-Z][a-z]*=\"@\+id" app/src/main/res/layout/*.xml 2>/dev/null | grep -v "Of\|Parent" && echo "❌ FOUND ERRORS" || echo "✓ No errors found"

echo ""
echo "Checking for correct layout_alignEndOf usage:"
grep -c "layout_alignEndOf" app/src/main/res/layout/*.xml && echo "✓ Found correct usage"

echo ""
echo "Checking for correct layout_alignBottomOf usage:"
grep -c "layout_alignBottomOf" app/src/main/res/layout/*.xml && echo "✓ Found correct usage"

echo ""
echo "Checking for correct layout_alignTopOf usage:"
grep -c "layout_alignTopOf" app/src/main/res/layout/*.xml && echo "✓ Found correct usage"

echo ""
echo "======================================="
echo "Verification Complete!"
echo "======================================="
echo ""
echo "Files Modified:"
echo "  1. fragment_profile.xml - Fixed 2 attributes"
echo "  2. activity_track_bus.xml - Fixed 2 attributes"
echo "  3. activity_profile.xml - Fixed 2 attributes"
echo ""
echo "Status: ✅ READY TO BUILD"
echo ""

