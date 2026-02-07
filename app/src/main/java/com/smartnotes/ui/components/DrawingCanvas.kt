package com.smartnotes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawingCanvas(
    paths: List<Path>,
    onPathAdded: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPath by remember { mutableStateOf(Path()) }
    var isDrawing by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPath = Path()
                        currentPath.moveTo(offset.x, offset.y)
                        isDrawing = true
                    },
                    onDrag = { change, _ ->
                        if (isDrawing) {
                            currentPath.lineTo(change.position.x, change.position.y)
                        }
                    },
                    onDragEnd = {
                        if (isDrawing) {
                            onPathAdded(currentPath)
                            currentPath = Path()
                            isDrawing = false
                        }
                    }
                )
            }
    ) {
        paths.forEach { path ->
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(
                    width = 5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        if (isDrawing) {
            drawPath(
                path = currentPath,
                color = Color.Black,
                style = Stroke(
                    width = 5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
